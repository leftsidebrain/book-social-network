package leftsidebrain.book.book;

import jakarta.persistence.EntityNotFoundException;
import leftsidebrain.book.common.PageResponse;
import leftsidebrain.book.exception.OperationNotPermittedException;
import leftsidebrain.book.file.FileStorageService;
import leftsidebrain.book.history.BookTransactionHistory;
import leftsidebrain.book.history.BookTransactionRepository;
import leftsidebrain.book.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookService {

	private final BookRepository bookRepository;
	private final BookTransactionRepository bookTransactionRepository;
	private final FileStorageService fileStorageService;
	private final BookMapper bookMapper;

	public Integer save(BookRequest request, Authentication connectedUser) {
		User user = ((User) connectedUser.getPrincipal());
		Book book = bookMapper.toBook(request);
		book.setOwner(user);
		return bookRepository.save(book).getId();
	}

	public BookResponse findById(Integer id) {
		return bookRepository.findById(id)
				.map(bookMapper::toBookResponse)
				.orElseThrow(() -> new EntityNotFoundException("Book not found, id: " + id));
	}

	public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
		User user = ((User) connectedUser.getPrincipal());
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
		List<BookResponse> bookResponses = books.
				stream()
				.map(bookMapper::toBookResponse)
				.toList();
		return new PageResponse<>(
				bookResponses,
				books.getNumber(),
				books.getSize(),
				books.getTotalElements(),
				books.getTotalPages(),
				books.isFirst(),
				books.isLast()
		);
	}

	public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
		User user = ((User) connectedUser.getPrincipal());
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()), pageable);

		List<BookResponse> bookResponses = books.
				stream()
				.map(bookMapper::toBookResponse)
				.toList();
		return new PageResponse<>(
				bookResponses,
				books.getNumber(),
				books.getSize(),
				books.getTotalElements(),
				books.getTotalPages(),
				books.isFirst(),
				books.isLast()
		);
	}

	public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
		User user = ((User) connectedUser.getPrincipal());
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<BookTransactionHistory> allBorrowedBooks = bookTransactionRepository.findAllBorrowedBooks(pageable, user.getId());
		List<BorrowedBookResponse> bookResponse = allBorrowedBooks.stream()
				.map(bookMapper::toBorrowedBookResponse)
				.toList();

		return new PageResponse<>(
				bookResponse,
				allBorrowedBooks.getNumber(),
				allBorrowedBooks.getSize(),
				allBorrowedBooks.getTotalElements(),
				allBorrowedBooks.getTotalPages(),
				allBorrowedBooks.isFirst(),
				allBorrowedBooks.isLast());
	}

	public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
		User user = ((User) connectedUser.getPrincipal());
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<BookTransactionHistory> allBorrowedBooks = bookTransactionRepository.findAllReturnedBooks(pageable, user.getId());
		List<BorrowedBookResponse> bookResponse = allBorrowedBooks.stream()
				.map(bookMapper::toBorrowedBookResponse)
				.toList();

		return new PageResponse<>(
				bookResponse,
				allBorrowedBooks.getNumber(),
				allBorrowedBooks.getSize(),
				allBorrowedBooks.getTotalElements(),
				allBorrowedBooks.getTotalPages(),
				allBorrowedBooks.isFirst(),
				allBorrowedBooks.isLast());
	}

	public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
		User user = ((User) connectedUser.getPrincipal());
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("Book not found, id: " + bookId));
		if (!Objects.equals(book.getOwner().getId(), user.getId())) {
			throw new OperationNotPermittedException("You cannot change the shareable status of another user's book.");
		}
		book.setShareable(!book.isShareable());
		return bookRepository.save(book).getId();
	}

	public Integer updateArchiveStatus(Integer bookId, Authentication connectedUser) {
		User user = ((User) connectedUser.getPrincipal());
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("Book not found, id: " + bookId));
		if (!Objects.equals( book.getOwner().getId(), user.getId())) {
			throw new OperationNotPermittedException("You cannot change the archive status of another user's book.");
		}
		book.setArchived(!book.isArchived());
		return bookRepository.save(book).getId();
	}

	public Integer borrowBook(Integer bookId, Authentication connectedUser) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("Book not found"));
		if (book.isArchived() || !book.isShareable()) {
			throw new OperationNotPermittedException("This book is not available for borrowing.");
		}
		User user = ((User) connectedUser.getPrincipal());
		if (Objects.equals(book.getOwner().getId(), user.getId())) {
			throw new OperationNotPermittedException("You cannot borrow your own book.");
		}
		final boolean isBorrowed = bookTransactionRepository.isBorrowedByUser(bookId, user.getId());
		if (isBorrowed) {
			throw new OperationNotPermittedException("You have already borrowed this book.");
		}
		BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
				.user(user)
				.book(book)
				.returned(false)
				.returnApproved(false)
				.build();
		return bookTransactionRepository.save(bookTransactionHistory).getId();
	}

	public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("Book not found"));
		if (book.isArchived() || !book.isShareable()) {
			throw new OperationNotPermittedException("This book is not available for borrowing.");
		}
		User user = ((User) connectedUser.getPrincipal());
		if (Objects.equals(book.getOwner().getId(),user.getId())) {
			throw new OperationNotPermittedException("You cannot borrow or return your own book.");
		}
		BookTransactionHistory bookTransactionHistory = bookTransactionRepository.findByBookIdAndUserId(bookId, user.getId()).orElseThrow(() ->
				new OperationNotPermittedException("You cannot borrow or return your"));
		bookTransactionHistory.setReturned(true);
		return bookTransactionRepository.save(bookTransactionHistory).getId();
	}

	public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("Book not found"));
		if (book.isArchived() || !book.isShareable()) {
			throw new OperationNotPermittedException("This book is not available for borrowing.");
		}
		User user = ((User) connectedUser.getPrincipal());
		if (!Objects.equals(book.getOwner().getId(),user.getId())) {
			throw new OperationNotPermittedException("You cannot borrow or return the book that you do not own.");
		}
		BookTransactionHistory bookTransactionHistory = bookTransactionRepository.findByBookIdAndOwnerId(bookId, user.getId()).orElseThrow(() ->
				new OperationNotPermittedException("The book is not returned"));
		bookTransactionHistory.setReturnApproved(true);
		return bookTransactionRepository.save(bookTransactionHistory).getId();
	}

	public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
		User user = ((User) connectedUser.getPrincipal());
		Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        if (!Objects.equals(book.getOwner().getId(),user.getId())) {
            throw new OperationNotPermittedException("You cannot upload a cover picture for another user's book.");
        }

		var bookCover = fileStorageService.saveFile(file, user.getId());
		book.setBookCover(bookCover);
		bookRepository.save(book);

	}
}
