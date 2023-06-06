package com.schoolmanagement.payload.Response;

import com.schoolmanagement.payload.Response.abstracts.BaseUserResponse;
import com.schoolmanagement.payload.request.abstracts.BaseUserRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class AdminResponse extends BaseUserResponse {
}
