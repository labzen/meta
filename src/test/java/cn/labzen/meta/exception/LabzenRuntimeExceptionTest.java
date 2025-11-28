package cn.labzen.meta.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LabzenRuntimeExceptionTest {

  @Test
  void testConstructorWithMessage() {
    String message = "Runtime exception occurred";
    LabzenRuntimeException exception = new LabzenRuntimeException(message);
    
    assertEquals(message, exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  void testConstructorWithMessageAndArgs() {
    LabzenRuntimeException exception = new LabzenRuntimeException("Invalid value: {}, expected: {}", 
        "abc", "123");
    
    assertEquals("Invalid value: abc, expected: 123", exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  void testConstructorWithCause() {
    IllegalArgumentException cause = new IllegalArgumentException("Invalid argument");
    LabzenRuntimeException exception = new LabzenRuntimeException(cause);
    
    assertEquals(cause, exception.getCause());
    assertEquals("java.lang.IllegalArgumentException: Invalid argument", exception.getMessage());
  }

  @Test
  void testConstructorWithCauseAndMessage() {
    NullPointerException cause = new NullPointerException("Null value");
    String message = "Processing failed";
    LabzenRuntimeException exception = new LabzenRuntimeException(cause, message);
    
    assertEquals(message, exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testConstructorWithCauseAndMessageWithArgs() {
    IllegalStateException cause = new IllegalStateException("Invalid state");
    LabzenRuntimeException exception = new LabzenRuntimeException(cause, 
        "Component {} is in state {}", "Config", "uninitialized");
    
    assertEquals("Component Config is in state uninitialized", exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testMessageFormattingWithEmptyArgs() {
    LabzenRuntimeException exception = new LabzenRuntimeException("Simple message");
    
    assertEquals("Simple message", exception.getMessage());
  }

  @Test
  void testIsRuntimeException() {
    LabzenRuntimeException exception = new LabzenRuntimeException("Test");
    
    assertTrue(exception instanceof RuntimeException);
  }
}
