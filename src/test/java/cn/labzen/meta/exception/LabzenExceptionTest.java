package cn.labzen.meta.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LabzenExceptionTest {

  @Test
  void testConstructorWithMessage() {
    String message = "Test exception message";
    LabzenException exception = new LabzenException(message);
    
    assertEquals(message, exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  void testConstructorWithMessageAndArgs() {
    LabzenException exception = new LabzenException("Error: {}, Code: {}", "Network timeout", 500);
    
    assertEquals("Error: Network timeout, Code: 500", exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  void testConstructorWithCause() {
    RuntimeException cause = new RuntimeException("Root cause");
    LabzenException exception = new LabzenException(cause);
    
    assertEquals(cause, exception.getCause());
    assertEquals("java.lang.RuntimeException: Root cause", exception.getMessage());
  }

  @Test
  void testConstructorWithCauseAndMessage() {
    RuntimeException cause = new RuntimeException("Root cause");
    String message = "Wrapped exception";
    LabzenException exception = new LabzenException(cause, message);
    
    assertEquals(message, exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testConstructorWithCauseAndMessageWithArgs() {
    RuntimeException cause = new RuntimeException("Root cause");
    LabzenException exception = new LabzenException(cause, "Failed to process {}", "request");
    
    assertEquals("Failed to process request", exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testMessageFormattingWithMultipleArgs() {
    LabzenException exception = new LabzenException("User: {}, Action: {}, Status: {}", 
        "admin", "login", "failed");
    
    assertEquals("User: admin, Action: login, Status: failed", exception.getMessage());
  }

  @Test
  void testMessageFormattingWithNullArgs() {
    LabzenException exception = new LabzenException("Value: {}", (Object) null);
    
    assertEquals("Value: null", exception.getMessage());
  }
}
