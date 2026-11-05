export default function MobileUserItem({ user, handleDelete }) {
    return (
        <div className="card p-2 mb-3 text-white bg-dark border border-white " >
            <div className="d-flex justify-content-between align-items-start">
                <h5 className="card-title">{user.name}</h5>
                <span className="badge text-bg-secondary rounded-pill ">{user.role}</span>
            </div>
            <div className="card-text mb-1">ID : {user.id}</div>
            <div className="card-text mb-3">{user.email}</div>
            <hr />
            <div className="d-flex justify-content-end">
                {user.role === 'USER' && (
                    <button
                        type="button"
                        className="btn btn-danger btn-sm rounded-pill px-3 "
                        onClick={() => { handleDelete(user.id) }}
                    >
                        DELETE
                    </button>
                )}

            </div>
        </div>
    )
}