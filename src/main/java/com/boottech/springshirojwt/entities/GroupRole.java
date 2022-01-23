/*
 * Copyright (c) 2019. All right reserved
 * Last Modified 28/06/19 07:40.
 * @aek
 *
 * www.sudcontractors.com
 *
 */

package com.boottech.springshirojwt.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * <h2>GroupRole</h2>
 *
 * @author aek
 *         <p>
 *         Description: group role for application
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
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
