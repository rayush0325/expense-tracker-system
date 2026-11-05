export default function DesktopUserList({ userList, handleDelete }) {
    return (
        <div className="table-responsive border border-secondary rounded">
            <table className="table table-hover table-dark align-middle mb-0 " >
                <thead>
                    <tr >
                        <th>Id</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    {userList.map((user) => {
                        return (
                            <tr key={user.id} >
                                <th>{user.id}</th>
                                <td>{user.name}</td>
                                <td>{user.email}</td>
                                <td>{user.role}</td>
                                <td>
                                    {user.role === 'USER' && (
                                        <button
                                            type="button"
                                            className="btn btn-danger"
                                            onClick={() => { handleDelete(user.id) }}>
                                            DELETE
                                        </button>
                                    )}

                                </td>
                            </tr>
                        )
                    })}
                </tbody>
            </table>
        </div>
    )
}