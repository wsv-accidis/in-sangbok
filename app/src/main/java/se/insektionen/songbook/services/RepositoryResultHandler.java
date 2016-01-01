package se.insektionen.songbook.services;

/**
 * Interface for a class that receives data from the repository.
 */
public interface RepositoryResultHandler<TResult> {
	void onError(int errorMessage);

	void onSuccess(TResult result);
}

