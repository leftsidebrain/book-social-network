package leftsidebrain.book.feedback;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import leftsidebrain.book.common.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedback")
public class FeedBackController {
	private final FeedBackService feedBackService;

	@PostMapping
	private ResponseEntity<Integer> saveFeedBack(
			@RequestBody @Valid FeedbackRequest feedbackRequest,
			Authentication connectedUser
	){
		return ResponseEntity.ok(feedBackService.save(feedbackRequest, connectedUser));
	}

	@GetMapping("/book/{book-id}")
	public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbackByBook(
			@PathVariable("book-id") Integer bookId,
			@RequestParam(name = "page", required = false,defaultValue = "0") int page,
			@RequestParam(name = "size", required = false,defaultValue = "0") int size,
			Authentication connectedUser

	){
		return ResponseEntity.ok(feedBackService.findAllFeedbackByBook(bookId, page, size, connectedUser));
	}
}
