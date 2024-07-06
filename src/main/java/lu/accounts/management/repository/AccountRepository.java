package lu.accounts.management.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lu.accounts.management.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
	Optional<Account> findByOwnerId(UUID ownerId);

	void deleteByOwnerId(UUID ownerId);
}
