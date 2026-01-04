package com.sliit.healthins;

import com.sliit.healthins.dto.ConfigDTO;
import com.sliit.healthins.dto.SystemMetricsDTO;
import com.sliit.healthins.dto.UserDTO;
import com.sliit.healthins.model.Role;
import com.sliit.healthins.model.User;
import com.sliit.healthins.repository.UserRepository;
import com.sliit.healthins.service.AdminService;
import com.sliit.healthins.util.PdfGeneratorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AdminServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PdfGeneratorUtil pdfGeneratorUtil;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUser_Success() {
        User user = new User();
        user.setUsername("tester");
        user.setEmail("test@example.com");
        user.setRole(Role.ADMIN);

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("tester");
        userDTO.setName("Test User");
        userDTO.setEmail("test@example.com");
        userDTO.setActive(true);
        userDTO.setRole(Role.ADMIN);
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);
        when(userRepository.save(user)).thenReturn(user);

        UserDTO createdUser = adminService.createUser(userDTO);

        assertNotNull(createdUser);
        assertEquals("tester", createdUser.getUsername());
        verify(userRepository, times(1)).save(user);
        verify(modelMapper, times(1)).map(user, UserDTO.class);
    }

    @Test
    public void testCreateUser_InvalidInput() {
        UserDTO invalidUser = new UserDTO();
        invalidUser.setUsername(null);
        assertThrows(IllegalArgumentException.class, () -> adminService.createUser(invalidUser));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    public void testCreateUser_DuplicateUsername() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("tester");
        userDTO.setName("Test User");
        userDTO.setEmail("test@example.com");
        userDTO.setActive(true);
        userDTO.setRole(Role.ADMIN);
        when(userRepository.existsByUsername("tester")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> adminService.createUser(userDTO));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    public void testGetUserById_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("tester");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> foundUser = adminService.getUserById(1L);

        assertTrue(foundUser.isPresent());
        assertEquals(1L, foundUser.get().getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> foundUser = adminService.getUserById(1L);

        assertFalse(foundUser.isPresent());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testDeleteUser_Success() {
        doNothing().when(userRepository).deleteById(1L);

        adminService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteUser_NotFound() {
        doThrow(new IllegalArgumentException("User not found")).when(userRepository).deleteById(1L);

        assertThrows(IllegalArgumentException.class, () -> adminService.deleteUser(1L));
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testUpdateUser_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("oldUser");
        UserDTO dto = new UserDTO();
        dto.setUsername("newUser");
        dto.setName("New User");
        dto.setEmail("new@example.com");
        dto.setActive(true);
        dto.setRole(Role.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserDTO.class)).thenReturn(dto);

        UserDTO updatedUser = adminService.updateUser(1L, dto);

        assertNotNull(updatedUser);
        assertEquals("newUser", updatedUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testGetSystemMetrics() {
        when(userRepository.count()).thenReturn(10L);
        when(userRepository.countByIsActiveTrue()).thenReturn(8L);
        SystemMetricsDTO metrics = adminService.getSystemMetrics();
        assertNotNull(metrics);
        assertEquals(10, metrics.getTotalUsers());
        assertEquals(8, metrics.getActiveUsers());
        verify(userRepository, times(1)).count();
        verify(userRepository, times(1)).countByIsActiveTrue();
    }

    @Test
    public void testUpdateConfig_Success() {
        ConfigDTO dto = new ConfigDTO("key", "value");
        ConfigDTO result = adminService.updateConfig(dto);
        assertNotNull(result);
        assertEquals("key", result.getKey());
        assertEquals("value", result.getValue());
    }

    @Test
    public void testUpdateConfig_InvalidInput() {
        ConfigDTO invalidDto = new ConfigDTO(null, "value");
        assertThrows(IllegalArgumentException.class, () -> adminService.updateConfig(invalidDto));
    }

    @Test
    public void testGenerateAuditLog_Success() throws IOException, IOException {
        when(pdfGeneratorUtil.generatePdf(anyString(), anyString())).thenReturn(new byte[0]);
        byte[] report = adminService.generateAuditLog(LocalDateTime.now().minusDays(1), LocalDateTime.now(), "ALL");
        assertNotNull(report);
        assertTrue(report.length > 0);
        verify(pdfGeneratorUtil, times(1)).generatePdf(anyString(), anyString());
    }

    @Test
    public void testGenerateAuditLog_InvalidDate() {
        assertThrows(IllegalArgumentException.class, () ->
                adminService.generateAuditLog(LocalDateTime.now(), LocalDateTime.now().minusDays(1), "ALL"));
    }
}