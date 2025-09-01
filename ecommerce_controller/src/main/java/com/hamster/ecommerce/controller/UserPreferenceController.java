package com.hamster.ecommerce.controller;

import com.hamster.ecommerce.mapper.UserPreferenceMapper;
import com.hamster.ecommerce.model.dto.UserPreferenceDTO;
import com.hamster.ecommerce.model.entity.UserPreference;
import com.hamster.ecommerce.service.UserPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Preference Controller")
@RestController
@RequestMapping("/user-preference")
public class UserPreferenceController
{

    private final UserPreferenceService userPreferenceService;
    private final UserPreferenceMapper userPreferenceMapper;

    public UserPreferenceController(UserPreferenceService userPreferenceService, UserPreferenceMapper userPreferenceMapper)
    {
        this.userPreferenceService = userPreferenceService;
        this.userPreferenceMapper = userPreferenceMapper;
    }

    @Operation(summary = "Get user preferences")
    @GetMapping()
    public ResponseEntity<UserPreferenceDTO> getUserPreference()
    {
        UserPreference userPreference = userPreferenceService.getUserPreferenceByUser();
        return ResponseEntity.ok(userPreferenceMapper.entityToDTO(userPreference));
    }

    @Operation(summary = "Edit user preferences")
    @PostMapping
    public ResponseEntity<UserPreferenceDTO> saveUserPreference(@RequestBody UserPreferenceDTO userSettingsDTO)
    {
        UserPreference savedUserPreference =
                userPreferenceService.saveUserPreference(userPreferenceMapper.dtoToEntity(userSettingsDTO));

        return ResponseEntity.ok(userPreferenceMapper.entityToDTO(savedUserPreference));
    }
}
