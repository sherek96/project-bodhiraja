package com.bodhiraja.bodhiraja.privilege;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "privilege")
public class Privilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 1 = Allowed, 0 = Denied
    @Column(name = "selec", nullable = false)
    private Boolean select; // Can View?

    @Column(name = "inser", nullable = false)
    private Boolean insert; // Can Add?

    @Column(name = "updat", nullable = false)
    private Boolean update; // Can Edit?

    @Column(name = "delet", nullable = false)
    private Boolean delete; // Can Delete?

    // --- RELATIONSHIPS ---

    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(optional = false)
    @JoinColumn(name = "module_id")
    private Module module;
}