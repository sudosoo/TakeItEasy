package com.sudosoo.takeiteasy.repository;

import com.sudosoo.takeiteasy.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {
}
