package com.dtalks.dtalks.board.comment.controller;

import com.dtalks.dtalks.board.comment.dto.CommentInfoDto;
import com.dtalks.dtalks.board.comment.dto.CommentRequestDto;
import com.dtalks.dtalks.board.comment.dto.UserCommentDto;
import com.dtalks.dtalks.board.comment.service.CommentService;
import com.dtalks.dtalks.exception.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Operation(summary = "특정 댓글 조회", responses = {
            @ApiResponse(responseCode = "404", description = "조회하려고 한 댓글이 db에 존재하지 않을 때", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<CommentInfoDto> searchOneComment(@PathVariable Long id) {
        CommentInfoDto commentInfoDto = commentService.searchById(id);
        return ResponseEntity.ok(commentInfoDto);
    }

    @Operation(summary = "특정 게시글의 댓글 리스트 조회", responses = {
            @ApiResponse(responseCode = "404", description = "게시글이 존재하지 않을 때", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @GetMapping("/list/post/{postId}")
    public ResponseEntity<List<CommentInfoDto>> searchPostCommentList(@PathVariable Long postId) {
        List<CommentInfoDto> list = commentService.searchListByPostId(postId);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "특정 유저의 댓글 리스트 조회 (페이지 사용, size = 10, sort=\"id\" desc 적용)" , parameters = {
            @Parameter(name = "nickname", description = "조회할 유저의 닉네임")
    }, responses = {
            @ApiResponse(responseCode = "400", description = "사용자가 존재하지 않을 때", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @GetMapping("/list/user/{nickname}")
    public ResponseEntity<Page<UserCommentDto>> searchUserIdCommentList(@PathVariable String nickname,
                                                                        @PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(commentService.searchListByNickname(nickname, pageable));
    }

    @Operation(summary = "특정 게시글에 댓글 저장", responses = {
            @ApiResponse(responseCode = "404", description = "게시글이 db에 존재하지 않을 때", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/{postId}")
    public ResponseEntity<Void> saveComment(@PathVariable Long postId, @Valid @RequestBody CommentRequestDto dto) {
        commentService.saveComment(postId, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "특정 게시글의 댓글에 대댓글 저장", description = "대댓글 저장시에 쓰이는 api(대댓글의 댓글도 가능). 계층형 댓글인것",
            parameters = {
                    @Parameter(name = "postId", description = "댓글을 달 게시글 id"),
                    @Parameter(name = "parentId", description = "대댓글을 달고자하는 댓글의 id -> 부모 댓글")
            }
            , responses = {
            @ApiResponse(responseCode = "404", description = "게시글이나 부모 댓글이 db에 존재하지 않을 때", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/{postId}/{parentId}")
    public ResponseEntity<Void> saveReComment(@PathVariable Long postId, @PathVariable Long parentId, @Valid @RequestBody CommentRequestDto dto) {
        commentService.saveReComment(postId, parentId, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "특정 댓글 수정", responses = {
            @ApiResponse(responseCode = "403", description = "해당 댓글을 수정할 수 있는 권한이 없을 때", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "댓글이 존재하지 않을 때", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateComment(@PathVariable Long id, @Valid @RequestBody CommentRequestDto dto) {
        commentService.updateComment(id, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "특정 댓글 삭제", responses = {
            @ApiResponse(responseCode = "403", description = "해당 댓글을 삭제할 수 있는 권한이 없을 때", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "댓글이 존재하지 않을 때, 게시글 작성자에게 해당 댓글이 작성됐다는 알림이 db에 존재하지 않을 때", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }
}
