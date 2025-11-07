package kr.co.ideait.platform.gaiacairos.web.entrypoint.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 쿠버네티스 readinessProbe 용
 * 2025 06 04 parkyk
 */
@RestController
public class DummyController {
	
	@ResponseBody
	@GetMapping("/login/dummy")
	public String dummy() {
		return "dummy";
	}
}
