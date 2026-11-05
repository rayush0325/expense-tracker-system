import MobileUserItem from "./MobileUserItem";

export default function MobileUserList({ userList , handleDelete}) {
    return (
        <div className="px-2">
            {userList.map((user) => {
                return <MobileUserItem key={user.id} user={user}  handleDelete={handleDelete}/>
            })}
        </div>
    )
}   