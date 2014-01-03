package com.in1.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import com.in1.model.TimeZone;

public interface TimeZoneRepository extends CrudRepository<TimeZone, Integer> {

	@Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public void delete(Integer id);
    
	@Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public void delete(Iterable<? extends TimeZone> entities);
    
	@Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public void delete(TimeZone entity);
    
	@Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public void deleteAll();
    
	@Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public Iterable<TimeZone> findAll();
    
	@Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public Iterable<TimeZone> findAll(Iterable<Integer> ids);

	@Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")    
    @Override
    public TimeZone findOne(Integer id);
    
	@Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public <S extends TimeZone> Iterable<S> save(Iterable<S> entities);
    
	@Transactional
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public <S extends TimeZone> S save(S entity);

	@Transactional(readOnly=true)
	public List<TimeZone> findById(Integer id);    
}
