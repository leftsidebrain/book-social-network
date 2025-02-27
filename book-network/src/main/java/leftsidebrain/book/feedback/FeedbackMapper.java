package leftsidebrain.book.feedback;

import leftsidebrain.book.book.Book;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {
	public Feedback toFeedback(FeedbackRequest feedbackRequest) {
		return Feedback.builder()
				.note(feedbackRequest.note())
				.comment(feedbackRequest.comment())
				.book(Book.builder()
						.id(feedbackRequest.bookId())
						.archived(false)
						.shareable(false)
						.build())
				.build();
	}

	public FeedbackResponse toFeedbackResponse(Feedback feedback, Integer id) {
		return FeedbackResponse.builder()
				.note(feedback.getNote())
				.comment(feedback.getComment())
				.ownFeedBack(Objects.equals(feedback.getCreatedBy(), id))
				.build();
	}
}
