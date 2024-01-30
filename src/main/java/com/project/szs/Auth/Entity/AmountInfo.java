package com.project.szs.Auth.Entity;


import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member")
@Getter
@Setter
@Builder
@Entity
public class AmountInfo {

    @Id
    @Column(name = "id")
    private Long id;


}
