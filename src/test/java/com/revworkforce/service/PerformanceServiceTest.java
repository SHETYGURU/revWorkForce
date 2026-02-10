package com.revworkforce.service;

import com.revworkforce.dao.PerformanceDAO;
import com.revworkforce.util.InputUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.sql.ResultSet;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

class PerformanceServiceTest {

    @Mock
    private PerformanceDAO mockDao;
    @Mock
    private ResultSet mockResultSet;

    private MockedStatic<InputUtil> mockedInputUtil;
    private MockedStatic<AuditService> mockedAuditService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Inject the mock DAO into the static field of PerformanceService
        setStaticField(PerformanceService.class, "dao", mockDao);
        // Also need to inject the cycleDAO mock if we were testing the
        // printActiveCycles interaction,
        // but for now we focus on the main dao. Note: Ideally process-related DAOs
        // should also be injected.

        mockedInputUtil = Mockito.mockStatic(InputUtil.class);
        mockedAuditService = Mockito.mockStatic(AuditService.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mockedInputUtil != null)
            mockedInputUtil.close();
        if (mockedAuditService != null)
            mockedAuditService.close();
        setStaticField(PerformanceService.class, "dao", null);
        setStaticField(PerformanceService.class, "dao", new PerformanceDAO());
    }

    /**
     * Test Case: Reviewing Team Members (Manager Goal)
     * Logic:
     * 1. Mock DAO to return an empty ResultSet (or one valid row) for team reviews.
     * 2. Mock User Input for Feedback and Rating.
     * 3. Verify DAO update method is called.
     */
    @Test
    void testReviewTeam() throws Exception {
        // Setup: Mock DAO to return ONE valid row to enter the loop, then false
        when(mockDao.getTeamReviews("MGR1")).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("review_id")).thenReturn(10);
        when(mockResultSet.getString("employee_name")).thenReturn("John Doe");

        // Mock User Inputs
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Review ID"))).thenReturn(10);
        mockedInputUtil.when(() -> InputUtil.readString(contains("Feedback"))).thenReturn("Good");
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Rating"))).thenReturn(5);

        // Execute
        PerformanceService.reviewTeam("MGR1");

        // Verify DAO submitManagerFeedback is called
        verify(mockDao).submitManagerFeedback(10, "Good", 5);

        // Verify Audit Log
        mockedAuditService
                .verify(() -> AuditService.log(eq("MGR1"), anyString(), anyString(), anyString(), anyString()));
    }

    /**
     * Test Case: Submitting a Self Review.
     * Logic:
     * 1. The Service calls cycleDAO.printActiveCycles() (not verified here, but
     * happens).
     * 2. User inputs Cycle ID, Deliverables, etc.
     * 3. DAO submits the review.
     */
    @Test
    void testSubmitSelfReview() throws Exception {
        // Mock User Inputs for the review form
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Cycle"))).thenReturn(2024);
        mockedInputUtil.when(() -> InputUtil.readString(contains("Deliverables"))).thenReturn("A");
        mockedInputUtil.when(() -> InputUtil.readString(contains("Accomplishments"))).thenReturn("B");
        mockedInputUtil.when(() -> InputUtil.readString(contains("Improvement"))).thenReturn("C");
        mockedInputUtil.when(() -> InputUtil.readString(contains("Self Rating"))).thenReturn("4.0");

        // Execute
        PerformanceService.submitSelfReview("EMP1");

        // Verification: Ensure DAO is called with the captured values
        verify(mockDao).submitSelfReview("EMP1", 2024, "A", "B", "C", 4.0);
    }

    @Test
    void testManageGoals() throws Exception {
        when(mockDao.getMyGoals("EMP1")).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        mockedInputUtil.when(() -> InputUtil.readInt(contains("Goal ID"))).thenReturn(100);
        mockedInputUtil.when(() -> InputUtil.readInt(contains("New progress"))).thenReturn(50);

        PerformanceService.manageGoals("EMP1");

        verify(mockDao).updateGoalProgress(100, 50);
    }

    @Test
    void testViewManagerFeedback_Failure() throws Exception {
        when(mockDao.getMyFeedback("EMP1")).thenThrow(new RuntimeException("DB Error"));

        PerformanceService.viewManagerFeedback("EMP1");

        verify(mockDao).getMyFeedback("EMP1");
    }

    @Test
    void testViewManagerFeedback() throws Exception {
        when(mockDao.getMyFeedback("EMP1")).thenReturn(mockResultSet);

        PerformanceService.viewManagerFeedback("EMP1");

        verify(mockDao).getMyFeedback("EMP1");
    }

    @Test
    void testReviewTeam_Failure() throws Exception {
        doThrow(new RuntimeException("DB Error")).when(mockDao).getTeamReviews("MGR1");

        PerformanceService.reviewTeam("MGR1");

        verify(mockDao).getTeamReviews("MGR1");
    }

    @Test
    void testSubmitSelfReview_Failure() throws Exception {
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Cycle"))).thenReturn(2024);
        mockedInputUtil.when(() -> InputUtil.readString(contains("Deliverables"))).thenReturn("A");
        mockedInputUtil.when(() -> InputUtil.readString(contains("Accomplishments"))).thenReturn("B");
        mockedInputUtil.when(() -> InputUtil.readString(contains("Improvement"))).thenReturn("C");
        mockedInputUtil.when(() -> InputUtil.readString(contains("Self Rating"))).thenReturn("4.0");

        doThrow(new RuntimeException("DB Error")).when(mockDao).submitSelfReview(anyString(), anyInt(), anyString(),
                anyString(), anyString(), anyDouble());

        PerformanceService.submitSelfReview("EMP1");

        verify(mockDao).submitSelfReview("EMP1", 2024, "A", "B", "C", 4.0);
    }

    @Test
    void testSubmitSelfReview_InvalidRating() throws Exception {
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Cycle"))).thenReturn(2024);
        mockedInputUtil.when(() -> InputUtil.readString(contains("Deliverables"))).thenReturn("A");
        mockedInputUtil.when(() -> InputUtil.readString(contains("Accomplishments"))).thenReturn("B");
        mockedInputUtil.when(() -> InputUtil.readString(contains("Improvement"))).thenReturn("C");
        mockedInputUtil.when(() -> InputUtil.readString(contains("Self Rating"))).thenReturn("invalid");

        PerformanceService.submitSelfReview("EMP1");

        verify(mockDao, never()).submitSelfReview(anyString(), anyInt(), anyString(), anyString(), anyString(),
                anyDouble());
    }

    @Test
    void testManageGoals_Failure() throws Exception {
        doThrow(new RuntimeException("DB Error")).when(mockDao).getMyGoals("EMP1");

        PerformanceService.manageGoals("EMP1");

        verify(mockDao).getMyGoals("EMP1");
    }

    private void setStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }
}
