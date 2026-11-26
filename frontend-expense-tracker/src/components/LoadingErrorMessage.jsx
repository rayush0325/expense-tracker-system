export default function LoadingErrorMessage({ onRetry, resourceName }) {
    return (
        <div className="d-flex flex-column align-items-center gap-3 text-center">
            <i className="bi bi-exclamation-circle fs-1 text-danger"></i>
            <h5>Unable to load {resourceName}</h5>
            <p >Something went wrong while fetching your {resourceName}. Please try again.</p>
            <button className="btn btn-outline-light" onClick={onRetry}>
                <i className="bi bi-arrow-repeat me-1"> </i> Try Again
            </button>

        </div>
    )
}