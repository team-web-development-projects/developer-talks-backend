package com.dtalks.dtalks.board.post.controller;

import com.dtalks.dtalks.board.post.dto.FavoriteAndRecommendStatusDto;
import com.dtalks.dtalks.board.post.dto.PutRequestDto;
import com.dtalks.dtalks.board.post.service.FavoritePostService;
import com.dtalks.dtalks.board.post.service.PostService;
import com.dtalks.dtalks.board.post.dto.PostDto;
import com.dtalks.dtalks.board.post.dto.PostRequestDto;
import com.dtalks.dtalks.board.post.service.RecommendPostService;
import com.dtalks.dtalks.exception.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final FavoritePostService favoritePostService;
    private final RecommendPostService recommendPostService;

    @Operation(summary = "특정 게시글 id로 조회, 조회수 + 1 작동", responses = {
            @ApiResponse(responseCode = "202", description = "관리자에 의해 접근 금지 처리 된 게시글", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "db에 존재하지 않는 게시글", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> searchById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.searchById(id));
    }


    @Operation(summary = "모든 게시글 조회 (페이지 사용, size = 10, sort=\"id\" desc 적용)")
    @GetMapping("/all")
    public ResponseEntity<Page<PostDto>> searchAll(@PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(postService.searchAllPost(pageable));
    }


    @Operation(summary = "특정 유저의 게시글 조회 (페이지 사용, size = 10, sort=\"id\" desc 적용)", parameters = {
            @Parameter(name = "nickname", description = "조회할 유저의 nickname")
    })
    @GetMapping("/list/user/{nickname}")
    public ResponseEntity<Page<PostDto>> searchPostsByUser(@PathVariable String nickname,
                                                           @PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(postService.searchPostsByUser(nickname, pageable));
    }


    @Operation(summary = "특정 유저의 즐겨찾기 게시글 조회 (페이지 사용, size = 10, sort=\"id\" desc 적용)", parameters = {
            @Parameter(name = "nickname", description = "조회할 유저의 nickname")
    })
    @GetMapping("/list/favorite/{nickname}")
    public ResponseEntity<Page<PostDto>> searchFavoritePostsByUser(@PathVariable String nickname,
                                                           @PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(favoritePostService.searchFavoritePostsByUser(nickname, pageable));
    }


    @Operation(summary = "검색어로 게시글 검색 (페이지 사용, size = 10, sort=\"id\" desc 적용)", description = "keyword가 제목(title), 내용(content)에 포함되면 검색 게시글에 추가됨")
    @GetMapping("/search")
    public ResponseEntity<Page<PostDto>> searchPosts(@RequestParam String keyword,
                                                    @PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(postService.searchByWord(keyword, pageable));
    }


    @Operation(summary = "추천수 베스트 5 게시글 가져오기 (리스트) - (조회 날짜 - 7일 이내 게시글 중 추천 수 1 이상)")
    @GetMapping("/best")
    public ResponseEntity<List<PostDto>> search5BestPosts() {
        return ResponseEntity.ok(postService.search5BestPosts());
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> createPost(@Valid PostRequestDto postDto) {
        return ResponseEntity.ok(postService.createPost(postDto));
    }


    @Operation(summary = "게시글 수정")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> updatePost(@Valid PutRequestDto putRequestDto,
                                           @PathVariable Long id) {
        return ResponseEntity.ok(postService.updatePost(putRequestDto, id));
    }


    @Operation(summary = "게시글 삭제", responses = {
            @ApiResponse(responseCode = "403", description = "해당 게시글을 삭제할 수 있는 권한이 없음. 게시글 작성자가 아님", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "202", description = "댓글이 존재하는 게시글은 삭제할 수 없음.", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글에 대한 사용자의 즐겨찾기, 추천 여부 확인, 로그인한 사용자일 경우에만 api 보내면 됨"
    , description = "게시글은 로그인 없이 조회가 가능하지만 로그인한 사용자의 경우에는 즐겨찾기, 추천 여부가 필요. 두 가지가 boolean 타입으로 나온다.")
    @GetMapping("/check/status/{postId}")
    public ResponseEntity<FavoriteAndRecommendStatusDto> checkFavoriteAndRecommendStatus(@PathVariable Long postId) {
        boolean favorite = favoritePostService.checkFavorite(postId);
        boolean recommend = recommendPostService.checkRecommend(postId);

        FavoriteAndRecommendStatusDto status = FavoriteAndRecommendStatusDto.builder()
                .favorite(favorite)
                .recommend(recommend)
                .build();
        return ResponseEntity.ok(status);
    }


    @Operation(summary = "게시글 즐겨찾기 설정", parameters = {
            @Parameter(name = "id", description = "즐겨찾기할 게시글의 id")
    }, responses = {
            @ApiResponse(responseCode = "202", description = "작성한 글에는 즐겨찾기 불가능, 이미 즐겨찾기로 설정되어 있음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 게시글이 db에 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/favorite/{id}")
    public ResponseEntity<Integer> favorite(@PathVariable Long id) {
        return ResponseEntity.ok(favoritePostService.favorite(id));
    }


    @Operation(summary = "게시글 즐겨찾기 취소", parameters = {
            @Parameter(name = "id", description = "즐겨찾기를 취소할 게시글의 id")
    }, responses = {
            @ApiResponse(responseCode = "404", description = "해당 게시글을 즐겨찾기 하지 않았음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/favorite/{id}")
    public ResponseEntity<Integer> unFavorite(@PathVariable Long id) {
        return ResponseEntity.ok(favoritePostService.unFavorite(id));
    }


    @Operation(summary = "게시글 추천", parameters = {
            @Parameter(name = "id", description = "추천할 게시글의 id")
    }, responses = {
            @ApiResponse(responseCode = "202", description = "작성한 글에는 추천 불가능, 이미 추천 설정되어 있음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 게시글이 db에 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/recommend/{id}")
    public ResponseEntity<Integer> recommend(@PathVariable Long id) {
        return ResponseEntity.ok(recommendPostService.recommend(id));
    }


    @Operation(summary = "게시글 추천 취소", parameters = {
            @Parameter(name = "id", description = "추천을 취소할 게시글의 id")
    }, responses = {
            @ApiResponse(responseCode = "404", description = "해당 게시글은 추천 상태가 아님 / 게시글이 존재하지 않음 / 게시글 작성자에게 추천 알림 기록이 없음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/recommend/{id}")
    public ResponseEntity<Integer> cancelRecommend(@PathVariable Long id) {
        return ResponseEntity.ok( recommendPostService.cancelRecommend(id));
    }
}
