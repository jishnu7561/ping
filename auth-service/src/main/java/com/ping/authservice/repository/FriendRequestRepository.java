package com.ping.authservice.repository;

import com.ping.authservice.model.FriendRequest;
import com.ping.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest,Integer> {

    FriendRequest findBySenderAndReceiver(User sender, User receiver);
}
