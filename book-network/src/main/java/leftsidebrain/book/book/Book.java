package leftsidebrain.book.book;

import jakarta.persistence.*;
import leftsidebrain.book.common.BaseEntity;
import leftsidebrain.book.feedback.Feedback;
import leftsidebrain.book.history.BookTransactionHistory;
import leftsidebrain.book.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Book extends BaseEntity {

	private String title;
	private String authorName;
	private String isbn;
	private String synopsis;
	private String bookCover;
	private boolean archived;
	private boolean shareable;


	@ManyToOne
	@JoinColumn(name = "owner_id")
	private User owner;

	@OneToMany(mappedBy = "book")
	private List<Feedback> feedbacks;

	@OneToMany(mappedBy = "book")
	private List<BookTransactionHistory> histories;

	@Transient
	public Double getRate(){
		if (feedbacks == null || feedbacks.isEmpty()){
			return 0.0;
		}
		var rate = this.feedbacks
				.stream()
				.mapToDouble(Feedback::getNote)
				.average()
				.orElse(0.0);
		return Math.round(rate*10.0) / 10.0;
	}


}
