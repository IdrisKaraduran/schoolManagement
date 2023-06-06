package com.schoolmanagement.payload.Response;

import com.schoolmanagement.payload.Response.abstracts.BaseUserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor//Neden AllArgConst yazmadik.Cunku SuperBuilder zaten yapiyor Bu yuzden yazmayi gerek gormedik AllArgs butun parametreli constructor olusturuyor
@SuperBuilder()
public class DeanResponse extends BaseUserResponse {
}
