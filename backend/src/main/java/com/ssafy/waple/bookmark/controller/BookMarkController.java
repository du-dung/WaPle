package com.ssafy.waple.bookmark.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import com.ssafy.waple.bookmark.dto.BookMarkDto;
import com.ssafy.waple.bookmark.dto.PlaceDto;
import com.ssafy.waple.bookmark.dto.SearchType;
import com.ssafy.waple.bookmark.service.BookMarkService;
import com.ssafy.waple.bookmark.service.PlaceService;
import com.ssafy.waple.common.PermissionCheck;

@CrossOrigin(origins = {"*"}, maxAge = 6000)
@RestController
@RequestMapping("/bookmarks")
@Api(value = "BookMark Controller", tags = "BookMark")
public class BookMarkController {
	private static final Logger logger = LoggerFactory.getLogger(BookMarkController.class);
	private static final PermissionCheck permissionCheck = new PermissionCheck();

	@Autowired
	BookMarkService service;

	@Autowired
	PlaceService placeService;

	@RequestMapping(method = RequestMethod.POST, produces = "application/json")
	@ApiOperation(value = "북마크 생성", notes = "북마크 생성하는 API")
	@ApiImplicitParam(name = "bookMark", value = "북마크 생성", required = true, dataTypeClass = BookMarkDto.class)
	@ApiResponses({
		@ApiResponse(code = 201, message = "북마크 생성 성공"),
		@ApiResponse(code = 401, message = "로그인 후 이용해 주세요"),
		@ApiResponse(code = 403, message = "권한이 없습니다"),
		@ApiResponse(code = 404, message = "북마크 생성 실패")
	})

	private ResponseEntity<?> create(@RequestBody BookMarkDto bookMark) {
		logger.debug("북마크 생성 호출");
		boolean success = service.create(bookMark);
		if (success) {
			return new ResponseEntity<>(HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	@ApiOperation(value = "북마크 조회", notes = "회원이 속한 모든 그룹, 모든 테마 북마크 조회", response = PlaceDto.class)
	@ApiImplicitParam(name = "searchType", value = "북마크 조회", required = true, dataTypeClass = SearchType.class)
	@ApiResponses({
		@ApiResponse(code = 201, message = "북마크 조회 성공"),
		@ApiResponse(code = 401, message = "로그인 후 이용해 주세요"),
		@ApiResponse(code = 403, message = "권한이 없습니다"),
		@ApiResponse(code = 404, message = "북마크 조회 실패")
	})
	private ResponseEntity<?> read(String searchType) {
		logger.debug("북마크 조회 시작");
		// front search data 양식
		/*
		let searchData = {
			userId: 123,
			groups: [
				{ groupId: 123, themeIds: [123, 456, 789]},
				{ groupId: 456, themeIds: [123, 456, 789]},
        	],
        	limit: 10, offset: 1
      	}
      */

		// 검색 데이터 저장
		SearchType type = new SearchType();

		try {
			logger.debug("쿼리스트링 파싱 시작");
			Gson gson = new Gson();
			String temp = URLDecoder.decode(searchType, "UTF-8");
			type = gson.fromJson(temp, SearchType.class);
			logger.debug("쿼리스트링 파싱 종료");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		boolean success = false;

		List<BookMarkDto> list = service.read(type);

		// 장소 저장
		Map<BookMarkDto, PlaceDto> result = new HashMap<>();

		if (list != null) {
			logger.debug("검색 결과가 있습니다.");
			success = true;
			for (BookMarkDto bookmark : list) {
				PlaceDto place = placeService.read(bookmark.getPlaceId());
				if (place != null) {
					result.put(bookmark, place);
				}
			}
		}
		if (success) {
			return new ResponseEntity<>(result, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{groupId}/{placeId}/{themeId}",
		produces = "application/json")
	@ApiOperation(value = "북마크 삭제", notes = "북마크 삭제 기능")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "groupId", value = "그룹 아이디", example = "1", required = true),
		@ApiImplicitParam(name = "placeId", value = "장소 아이디", example = "19781214", required = true),
		@ApiImplicitParam(name = "themeId", value = "테마 아이디", example = "1", required = true),
		@ApiImplicitParam(name = "token", value = "토큰", example = "das4dq-84AET68M1s", required = true)
	})
	@ApiResponses({
		@ApiResponse(code = 204, message = "북마크 삭제 성공"),
		@ApiResponse(code = 401, message = "로그인 후 이용해 주세요"),
		@ApiResponse(code = 403, message = "권한이 없습니다"),
		@ApiResponse(code = 404, message = "북마크 삭제 실패")
	})
	private ResponseEntity<?> delete(@PathVariable("groupId") int groupId, @PathVariable("placeId") String placeId,
		@PathVariable("themeId") int themeId, @RequestHeader(value = "token") String token) {
		System.out.println(token);
		long userId = permissionCheck.check(token).getUserId();
		boolean success = service.delete(userId, themeId, groupId, placeId);
		if (success) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
