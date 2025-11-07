package kr.co.ideait.platform.gaiacairos.comp.system;


import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import kr.co.ideait.platform.gaiacairos.comp.project.service.InformationService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.BoardService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BoardComponent extends AbstractComponent {

	@Autowired
    BoardService boardService;

	@Autowired
	InformationService informationService;

	@Autowired
    BoardDto boardDto;

	/**
	 * 게시글 생성
	 */
	@Transactional
	public boolean createBoard(BoardForm.Board board, List<MultipartFile> files, CommonReqVo commonReqVo) {
		SmBoard returnBoard = boardService.createBoard(board, files, commonReqVo);
		if ( returnBoard != null) {
			if("Y".equals(commonReqVo.getApiYn())) {
				//CAIROS 일 때
				if(PlatformType.CAIROS.getName().equals(platform)){
					//CAIROS 이면서 공지사항 일 때
					if ("1".equals(returnBoard.getBoardType())){
						//전체 공지일 때
						if("1".equals(board.getBoardDiv())){
							board.setBoardCd(returnBoard.getBoardCd());

							Map<String, Object> invokeParams = Maps.newHashMap();
							invokeParams.put("board", board);
							invokeParams.put("userParam", commonReqVo.getUserParam());

							Map response = invokeCairos2Pgaia("CAGAM07030601", invokeParams, files);

							if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
								throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
							}
						}
						//프로젝트 혹은 계약 공지
						else{
							List<BoardForm.ReceptionItem> receptionItemList =  board.getReceptionList();
							boolean isPgaia = false;
							if(receptionItemList.get(0) != null){
								CnProject project = informationService.getProject(receptionItemList.get(0).getPjtNo());
								isPgaia = "P".equals(project.getPjtDiv());
							}

							//PGaia 프로젝트이고 공유 "Y" 일 때 C2P
							if (isPgaia && "Y".equals(returnBoard.getShareYn())) {
								// API 통신
								board.setBoardCd(returnBoard.getBoardCd());

								Map<String, Object> invokeParams = Maps.newHashMap();
								invokeParams.put("board", board);
								invokeParams.put("userParam", commonReqVo.getUserParam());

								Map response = invokeCairos2Pgaia("CAGAM07030601", invokeParams, files);

								if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
									throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
								}
							}
						}
					}
					//CAIROS 이면서 FAQ 일 때는 전부 C2P
					else if("2".equals(returnBoard.getBoardType())){
						board.setBoardCd(returnBoard.getBoardCd());

						Map<String, Object> invokeParams = Maps.newHashMap();
						invokeParams.put("board", board);
						invokeParams.put("userParam", commonReqVo.getUserParam());

						Map response = invokeCairos2Pgaia("CAGAM07030601", invokeParams, files);

						if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
							throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
						}
					}
				}
				//PGAIA 접속중이면 무조건 P2C
				else if(PlatformType.PGAIA.getName().equals(platform)){
					board.setBoardCd(returnBoard.getBoardCd());

					Map<String, Object> invokeParams = Maps.newHashMap();
					invokeParams.put("board", board);
					invokeParams.put("userParam", commonReqVo.getUserParam());

					Map response = invokePgaia2Cairos("GACAM07030601", invokeParams, files);

					if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
						throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
					}
				}
			}
			return true;
		}
		throw new GaiaBizException(ErrorType.ETC,"Logical Issue");
	}

	/**
	 * 게시글 수정
	 */
	@Transactional
	public SmBoard updateBoard(SmBoard smBoard, List<SmBoardReception> smBoardReceptionList, List<SmPopupMsg> smPopupMsgList, List<MultipartFile> files, String preShareYn,String pjtDiv, String apiYn) {
		for(SmBoardReception smBoardReception : smBoardReceptionList) {
			smBoardReception.setChgId(UserAuth.get(true).getUsrId());
		}
		SmBoard returnBoard = boardService.updateBoard(smBoard, smBoardReceptionList, smPopupMsgList, files, preShareYn, UserAuth.get(true).getUsrId());
		if(returnBoard != null){
			if("Y".equals(apiYn)) {
				//CAIROS 일 때
				if(PlatformType.CAIROS.getName().equals(platform)){
					//CAIROS 이면서 공지사항 일 때
					if ("1".equals(returnBoard.getBoardType())){
						boolean isPgaia = false;
						if(smBoardReceptionList.get(0) != null){
							if("1".equals(smBoardReceptionList.get(0).getBoardDiv())){
								Map<String, Object> invokeParams = Maps.newHashMap();
								invokeParams.put("board", returnBoard);
								invokeParams.put("smBoardReceptionList", smBoardReceptionList);
								invokeParams.put("smPopupMsgList", smPopupMsgList);
								invokeParams.put("preShareYn", preShareYn);

								Map response = invokeCairos2Pgaia("CAGAM07030602", invokeParams, files);

								if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
									throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
								}
							}
							else {
								CnProject project = informationService.getProject(smBoardReceptionList.get(0).getPjtNo());
								isPgaia = "P".equals(project.getPjtDiv());
							}
						}
						//PGaia 프로젝트이고 공유 "Y" 일 때 C2P
						if (isPgaia && "Y".equals(returnBoard.getShareYn())) {
							// API 통신
							Map<String, Object> invokeParams = Maps.newHashMap();
							invokeParams.put("board", returnBoard);
							invokeParams.put("smBoardReceptionList", smBoardReceptionList);
							invokeParams.put("smPopupMsgList", smPopupMsgList);
							invokeParams.put("preShareYn", preShareYn);

							Map response = invokeCairos2Pgaia("CAGAM07030602", invokeParams, files);

							if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
								throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
							}
						}
					}
					//CAIROS 이면서 FAQ 일 때는 전부 C2P
					else if("2".equals(returnBoard.getBoardType())){
						Map<String, Object> invokeParams = Maps.newHashMap();
						invokeParams.put("board", returnBoard);
						invokeParams.put("smBoardReceptionList", smBoardReceptionList);
						invokeParams.put("smPopupMsgList", smPopupMsgList);
						invokeParams.put("preShareYn", preShareYn);

						Map response = invokeCairos2Pgaia("CAGAM07030602", invokeParams, files);

						if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
							throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
						}
					}
				}
				//PGAIA이면 무조건 P2C
				else if(PlatformType.PGAIA.getName().equals(platform)){
					Map<String, Object> invokeParams = Maps.newHashMap();
					invokeParams.put("board", returnBoard);
					invokeParams.put("smBoardReceptionList", smBoardReceptionList);
					invokeParams.put("smPopupMsgList", smPopupMsgList);
					invokeParams.put("preShareYn", preShareYn);

					Map response = invokePgaia2Cairos("GACAM07030602", invokeParams, files);

					if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
						throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
					}
				}
			}
			return returnBoard;
		}

		return returnBoard;
	}

	/**
	 * 게시글 삭제
	 */
	@Transactional
	public boolean deleteBoard(List<String> boardCdList,String pjtDiv, String userId,String apiYn) {
		Map response = Maps.newHashMap();

		List<SmBoard> serviceResult = boardService.deleteBoard(boardCdList,userId);
		List<String> deleteBoardCdList = new ArrayList<>();
		if(!serviceResult.isEmpty()){
			if("Y".equals(apiYn)) {
				HashMap<String,Object> apiParams = new HashMap<>();
				//CAIROS 일 때
				if(PlatformType.CAIROS.getName().equals(platform)){
					for(SmBoard smBoard : serviceResult){
						//지워진 게시글이 공지사항 일 때
						if ("1".equals(smBoard.getBoardType())){
							boolean isPgaia = false;
							List<SmBoardReception> smBoardReceptionList = boardService.getSmBoardReceptionList(smBoard.getBoardCd());
							if(smBoardReceptionList != null && smBoardReceptionList.size() > 0){
								//전체 공지일 때
								if("1".equals(smBoardReceptionList.get(0).getBoardDiv())){
									deleteBoardCdList.add(smBoard.getBoardCd());
								}
								//프로젝트나 계약 공지일 때
								else{
									//
									isPgaia = informationService.isPgaia(smBoardReceptionList.get(0).getPjtNo());
								}
							}
							//PGaia 프로젝트이고 공유 "Y" 일 때 C2P
							if (isPgaia && "Y".equals(smBoard.getShareYn())) {
								deleteBoardCdList.add(smBoard.getBoardCd());
							}
						}
						//CAIROS 이면서 FAQ 일 때는 전부 C2P
						else if("2".equals(smBoard.getBoardType())){
							deleteBoardCdList.add(smBoard.getBoardCd());
						}
					}
					apiParams.put("boardCdList", deleteBoardCdList);
					apiParams.put("userId", userId);
					response = invokeCairos2Pgaia("CAGAM07030603",apiParams);
					if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
						throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
					}
				}
				//PGAIA이면 무조건 P2C
				else if(PlatformType.PGAIA.getName().equals(platform)){
					for(SmBoard smBoard : serviceResult){
						deleteBoardCdList.add(smBoard.getBoardCd());
					}
					apiParams.put("boardCdList", deleteBoardCdList);
					apiParams.put("userId", userId);
					response = invokePgaia2Cairos("CAGAM07030603",apiParams);
					if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
						throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
					}
				}
			}
			return true;
		}
		return false;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map receiveInterfaceService(String transactionId, Map params) {
		Map<String, Object> result = Maps.newHashMap();
		result.put("resultCode", "00");

		try {
			if ("CAGAM07030601".equals(transactionId) || "GACAM07030601".equals(transactionId)) {
				BoardForm.Board board = objectMapper.convertValue(params.get("board"), BoardForm.Board.class);
				List<MultipartFile> files = (List<MultipartFile>)params.get("files");
				String[] userParam = objectMapper.convertValue(params.get("userParam"), String[].class);

				CommonReqVo commonReqVo = new CommonReqVo();
				commonReqVo.setUserParam(userParam);

				boardService.createBoard(board, files, commonReqVo);

				return result;
			}
			else if ("CAGAM07030602".equals(transactionId) || "GACAM07030602".equals(transactionId)) {
				SmBoard board = objectMapper.convertValue(params.get("board"), SmBoard.class);
				List<SmBoardReception> smBoardReceptionList = objectMapper.convertValue(params.get("smBoardReceptionList"), new TypeReference<List<SmBoardReception>>() {});
				List<SmPopupMsg> smPopupMsgList = objectMapper.convertValue(params.get("smPopupMsgList"), new TypeReference<List<SmPopupMsg>>() {});
				String preShareYn = objectMapper.convertValue(params.get("preShareYn"), String.class);
				List<MultipartFile> files = (List<MultipartFile>)params.get("files");

				CommonReqVo commonReqVo = new CommonReqVo();

				boardService.updateBoard(board, smBoardReceptionList, smPopupMsgList, files, preShareYn, commonReqVo.getUserId());

				return result;
			}
			else if ("CAGAM07030603".equals(transactionId) || "GACAM07030603".equals(transactionId)) {
				List<String> boards = objectMapper.convertValue(params.get("boardCdList"), new TypeReference<List<String>>() {});
				String dltId = objectMapper.convertValue(params.get("userId"), String.class);

				boardService.deleteBoard(boards, dltId);

				return result;
			}
		} catch (GaiaBizException e) {
			log.error(e.getMessage(), e);
			result.put("resultCode", "01");
			result.put("resultMsg", e.getMessage());
		}

		return result;
	}
}
