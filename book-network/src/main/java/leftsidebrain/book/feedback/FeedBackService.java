package leftsidebrain.book.feedback;

import jakarta.persistence.EntityNotFoundException;
import leftsidebrain.book.book.Book;
import leftsidebrain.book.book.BookRepository;
import leftsidebrain.book.common.PageResponse;
import leftsidebrain.book.exception.OperationNotPermittedException;
import leftsidebrain.book.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedBackService {
	private final BookRepository bookRepository;
	private final FeedbackMapper feedbackMapper;
	private final FeedbackRepository feedbackRepository;

	public Integer save(FeedbackRequest feedbackRequest, Authentication connectedUser) {
		Book book = bookRepository.findById(feedbackRequest.bookId())
				.orElseThrow(() -> new EntityNotFoundException("Book not found"));
		if (book.isArchived() || !book.isShareable()) {
			throw new OperationNotPermittedException("You Cant give a feedback for and archive or not shareable book");
		}
		User user = ((User) connectedUser.getPrincipal());
		if (Objects.equals(book.getOwner().getId(), user.getId())) {
			throw new OperationNotPermittedException("You cannot give a feedback to your own book.");
		}
		Feedback feedback = feedbackMapper.toFeedback(feedbackRequest);
		return feedbackRepository.save(feedback).getId();
	}

	public PageResponse<FeedbackResponse> findAllFeedbackByBook(Integer bookId, int page, int size, Authentication connectedUser) {
		Pageable pageable = PageRequest.of(page, size);
		User user = ((User) connectedUser.getPrincipal());
		Page<Feedback> feedbacks = feedbackRepository.findAllByBookId(bookId, pageable);
		List<FeedbackResponse> feedbackResponses = feedbacks.stream()
				.map(f -> feedbackMapper.toFeedbackResponse(f, user.getId()))
				.toList();
		return new PageResponse<>(
				feedbackResponses,
				feedbacks.getNumber(),
				feedbacks.getSize(),
				feedbacks.getTotalElements(),
				feedbacks.getTotalPages(),
				feedbacks.isFirst(),
				feedbacks.isLast()
		);
	}
}
