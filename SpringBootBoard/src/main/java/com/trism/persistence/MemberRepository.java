package com.trism.persistence;

import org.springframework.data.repository.CrudRepository;
import com.trism.domain.Member;

public interface MemberRepository extends CrudRepository<Member, String> {

	
}
