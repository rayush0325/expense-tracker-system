import ConfirmDeleteModal from "./ConfirmDeleteModal";
import DesktopUserList from "./DesktopUserList";
import MobileUserList from "./MobileUserList";

export default function UserList({ userList , handleDelete}) {
    return (
        <div >
            <div className="d-none d-md-block">
                <DesktopUserList userList={userList} handleDelete={handleDelete}/>
            </div>
            <div className="d-block d-md-none">
                <MobileUserList userList={userList} handleDelete={handleDelete} />
            </div>
        </div>
    )
}