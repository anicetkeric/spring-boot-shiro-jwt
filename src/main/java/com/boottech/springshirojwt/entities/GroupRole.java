package com.boottech.springshirojwt.entities;

import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * <h2>GroupRole</h2>
 *
 * @author aek
 *         <p>
 *         Description: group role for application
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class GroupRole implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@NotNull
	@Column(unique = true)
	@Size(min = 1, max = 50)
	private String code;

	@NotNull
	private String permissions;
}
