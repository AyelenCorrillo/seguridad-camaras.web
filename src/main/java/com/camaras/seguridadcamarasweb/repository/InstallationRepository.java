package com.camaras.seguridadcamarasweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.camaras.seguridadcamarasweb.model.Installation;



public interface InstallationRepository extends JpaRepository<Installation, Long> {
}
