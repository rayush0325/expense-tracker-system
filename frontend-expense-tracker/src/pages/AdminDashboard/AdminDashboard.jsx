import { useContext, useEffect, useState } from "react"
import { UserContext } from "../../context/UserContext";
import apiClient from "../../services/apiClient";
import { handleApiError } from "../../utils/handleApiError";
import UserList from "../../components/UserList";
import ConfirmDeleteModal from "../../components/ConfirmDeleteModal";

export default function AdminDashboard() {
    const [userList, setUserList] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showDialog, setShowDialog] = useState(false);
    const [userId, setUserId] = useState();
    const [deleting, setDeleting] = useState(false);

    console.log(userList);
    useEffect(() => {
        getUserList()
    }, []);

    async function getUserList() {
        setLoading(true);
        try {

            console.log("fetching user list");
            const response = await apiClient.get("/admin/users/");
            setUserList(response.data);

        } catch (error) {
            console.error("error occurred in fetching user list");
            handleApiError(error);
        } finally {
            setLoading(false);
        }
    }

    function handleDelete(id) {
        console.log(`user with id ${id} is deleted`);
        setUserId(id);
        setShowDialog(true);

    }

    async function handleConfirmDelete(id) {
        setDeleting(true);
        try {
            await deleteUser(id);
            await getUserList();

        } catch (error) {
            handleApiError(error);
        } finally {
            setDeleting(false);
            setShowDialog(false);
        }
    }

    function handleCancel() {
        setShowDialog(false);
    }

    async function deleteUser(id) {
        await apiClient.delete(`/admin/users/${id}`);
    }



    return (
        <div className="min-vh-100 bg-dark text-white p-2">
            <h1 className="my-3 p-2 border-bottom border-white">Admin Dashboard</h1>
            <div className="p-2 overflow-x-auto">
                {loading ? (
                    <h2>Loading users...</h2>
                ) : (userList.length === 0)
                    ? (
                        <h2>no users yet</h2>
                    ) : (
                        <>
                            <div className="d-flex justify-content-between align-items-center mb-3 ">

                                <h3 className="mb-0">Users</h3>
                                <span className="badge text-bg-secondary fs-6">
                                    {userList.length}
                                </span>
                            </div>
                            <UserList userList={userList} handleDelete={handleDelete} />
                        </>
                    )
                }
                <ConfirmDeleteModal
                    userId={userId}
                    show={showDialog}
                    handleConfirmDelete={handleConfirmDelete}
                    handleCancel={handleCancel}
                    deleting={deleting}
                />
            </div>
        </div>
    )
}