package com.misr.ais.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.misr.ais.configuration.Routes;
import com.misr.ais.dto.apiresponse.ApiResponse;
import com.misr.ais.dto.land.ConfigureLandRequest;
import com.misr.ais.dto.land.LandRequest;
import com.misr.ais.helper.ApiResponseHelper;
import com.misr.ais.model.Land;
import com.misr.ais.service.LandService;

import jakarta.validation.Valid;

@RestController
public class LandController {

  private final LandService landService;

  public LandController(LandService landService) {
    this.landService = landService;
  }

  @PostMapping(Routes.Land.Index)
  public ApiResponse<Land> add(@Valid @RequestBody LandRequest body) {
    var response = landService.add(body);
    return ApiResponseHelper.build(response);
  }

  @PutMapping(Routes.Land.ResourceById)
  public ApiResponse<Land> edit(@PathVariable Long id, @Valid @RequestBody LandRequest body) {
    var response = landService.edit(id, body);
    return ApiResponseHelper.build(response);
  }

  @GetMapping(Routes.Land.Index)
  public ApiResponse<List<Land>> list(@RequestParam Map<String, String> params, Pageable pageable) {
    var response = landService.list(pageable);
    return ApiResponseHelper.build(response);
  }

  @PostMapping(Routes.Land.Configurations)
  public ApiResponse<Land> configure(@PathVariable Long id, @Valid @RequestBody ConfigureLandRequest body) {
    var response = landService.configure(id, body);
    return ApiResponseHelper.build(response);
  }

}
