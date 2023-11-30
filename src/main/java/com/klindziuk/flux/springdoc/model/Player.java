/*
 * Copyright (c) 2022. Dandelion tutorials
 */

package com.klindziuk.flux.springdoc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Player {

  @Id
  private Long id;
  private String name;
  private Integer age;
  private String club;
  private String nationality;

  public Player(Player player) {
    this.name = Objects.requireNonNull(player.getName());
    this.age = Objects.requireNonNull(player.getAge());
    this.club = Objects.requireNonNull(player.getClub());
    this.nationality = Objects.requireNonNull(player.getNationality());
  }
}
