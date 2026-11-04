import DesktopUserList from "./DesktopUserList";
import MobileUserList from "./MobileUserList";

export default function UserList({ userList }) {
    return (
        <div >
            <div className="d-none d-md-block">
                <DesktopUserList userList={userList} />
            </div>
            <div className="d-block d-md-none">
                <MobileUserList userList={userList} />
            </div>

        </div>
    )
}