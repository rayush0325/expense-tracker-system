import MobileUserItem from "./MobileUserItem";

export default function MobileUserList({ userList }) {
    return (
        <div className="px-2">
            {userList.map((user) => {
                return <MobileUserItem key={user.id} user={user} />
            })}
        </div>
    )
}   