export default function ConfirmDeleteModal({
    id,
    handleCancel,
    handleConfirmDelete,
    deleting
}) {
    return (
        <div className="position-fixed top-0 start-0 w-100 vh-100
                bg-dark bg-opacity-50
                d-flex justify-content-center align-items-center text-white"
            style={{ zIndex: 1055 }}>
            <div className="bg-dark border border-white rounded-4 p-4  w-75" 
                style={{ maxWidth: "400px" }}>

                <h5>Confirm Deletion</h5>

                <p>
                    Are you sure you want to delete?
                    This action cannot be undone.
                </p>

                <div className="d-flex justify-content-end gap-2">
                    <button className="btn btn-secondary" onClick={handleCancel}>
                        Cancel
                    </button>

                    <button
                        className="btn btn-danger"
                        onClick={() => { handleConfirmDelete(id) }}
                        disabled = {deleting ? true : false}
                    >
                        {deleting ? "DELETING..." : "DELETE"}
                    </button>
                </div>
            </div>
        </div>
    )
}