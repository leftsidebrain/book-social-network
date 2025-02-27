package leftsidebrain.book.book;

import leftsidebrain.book.file.FileUtils;
import leftsidebrain.book.history.BookTransactionHistory;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {

	public Book toBook(BookRequest request) {
		return Book.builder()
                .id(request.id())
				.title(request.title())
				.isbn(request.isbn())
				.authorName(request.authorName())
				.synopsis(request.synopsis())
				.archived(false)
				.shareable(request.shareable())
                .build();
	}

	public BookResponse toBookResponse(Book book) {
		return BookResponse.builder()
				.id(book.getId())
				.title(book.getTitle())
				.authorName(book.getAuthorName())
				.synopsis(book.getSynopsis())
				.archived(book.isArchived())
				.shareable(book.isShareable())
				.isbn(book.getIsbn())
				.rate(book.getRate())
				.owner(book.getOwner().getFullName())
				.cover(FileUtils.readFileFromLocation(book.getBookCover()))
				.build();
	}

	public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory history) {
		return BorrowedBookResponse.builder()
				.id(history.getBook().getId())
				.title(history.getBook().getTitle())
				.authorName(history.getBook().getAuthorName())
				.isbn(history.getBook().getIsbn())
				.rate(history.getBook().getRate())
				.returned(history.isReturned())
				.returnApproved(history.isReturnApproved())
				.build();
	}
}
