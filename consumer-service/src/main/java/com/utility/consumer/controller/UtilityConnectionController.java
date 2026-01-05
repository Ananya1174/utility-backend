package com.utility.consumer.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.utility.consumer.dto.response.ConnectionResponseDto;
import com.utility.consumer.service.UtilityConnectionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/connections")
@RequiredArgsConstructor
public class UtilityConnectionController {

	private final UtilityConnectionService connectionService;

	@GetMapping
	@PreAuthorize("hasRole('CONSUMER')")
	public List<ConnectionResponseDto> getMyConnections() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String userId = authentication.getName();

		return connectionService.getConnectionsByUserId(userId);
	}

	@GetMapping("/{id}")
	public ConnectionResponseDto getConnectionById(@PathVariable("id") String id) {

		return connectionService.getConnectionById(id);
	}
	@GetMapping("/internal/all")
	public List<ConnectionResponseDto> getAllConnections() {
	    return connectionService.getAllConnections();
	}
}