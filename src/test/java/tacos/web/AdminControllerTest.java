package tacos.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void testAdminDashboardRequiresAdminRole() throws Exception {
    // Unauthenticated user should be redirected to login
    mockMvc.perform(get("/admin"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("**/login"));
  }

  @Test
  @WithMockUser(roles = "USER")
  public void testAdminDashboardDeniedForRegularUser() throws Exception {
    // Regular user should get access denied
    mockMvc.perform(get("/admin"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void testAdminDashboardAccessibleForAdmin() throws Exception {
    // Admin user should access the dashboard
    mockMvc.perform(get("/admin"))
        .andExpect(status().isOk())
        .andExpect(view().name("admin/dashboard"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void testAdminDashboardDisplaysOrderCount() throws Exception {
    // Admin dashboard should display order count
    mockMvc.perform(get("/admin"))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("orderCount"));
  }

  @Test
  @WithMockUser(roles = "USER")
  public void testDeleteOrdersRequiresAdminRole() throws Exception {
    // Regular user should not be able to delete orders
    mockMvc.perform(post("/admin/deleteOrders").with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void testDeleteOrdersRedirectsToAdmin() throws Exception {
    // Admin should be able to delete orders and be redirected
    mockMvc.perform(post("/admin/deleteOrders").with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/admin"));
  }

  @Test
  public void testDeleteOrdersRequiresAuthentication() throws Exception {
    // Unauthenticated user should be redirected to login
    mockMvc.perform(post("/admin/deleteOrders").with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("**/login"));
  }
}
