package com.library.requestDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendNotificationRequestDTO {
	
	@NotNull(message = "Username can't be null")
    private String userName;
	
	@NotNull(message = "Title can't be empty")
    private String title;
	
	@NotNull(message = "Message can't be empty")
    private String message;
}
