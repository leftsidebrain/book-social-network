package leftsidebrain.book.feedback;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import leftsidebrain.book.book.Book;
import leftsidebrain.book.common.BaseEntity;
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
public class Feedback extends BaseEntity {

	private Double note;
	private  String comment;


	@ManyToOne()
	@JoinColumn(name = "book_id")
	private Book book;


}
