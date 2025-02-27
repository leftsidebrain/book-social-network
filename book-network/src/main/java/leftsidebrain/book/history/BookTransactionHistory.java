package leftsidebrain.book.history;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import leftsidebrain.book.book.Book;
import leftsidebrain.book.common.BaseEntity;
import leftsidebrain.book.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BookTransactionHistory extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;


	@ManyToOne
	@JoinColumn(name = "book_id")
	private Book book;

	private boolean returned;
	private boolean returnApproved;


}
