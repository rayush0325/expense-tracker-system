import "./DesktopExpenseList.css";
export default function DesktopExpenseList({ expenseList, handleExpenseUpdate, handleExpenseDelete }) {
    return (
        <div className="border rounded-3 overflow-auto p-3">
            <table className="table table-dark table-borderless table-hover align-middle  mb-0">
                <thead >
                    <tr className="  shadow-lg p-3 mb-5 bg-body-tertiary rounded fs-5 ">
                        <th>expenseDate</th>
                        <th>description</th>
                        <th>category</th>
                        <th>amount</th>
                        <th className="align-items-center">action</th>
                    </tr>
                </thead>
                <tbody>
                    {expenseList.map((expense) => {
                        return (
                            <tr className="shadow-lg p-3 mb-5 bg-body-tertiary rounded " key={expense.id}>
                                <td>{expense.expenseDate}</td>
                                <td>{expense.description}</td>
                                <td>{expense.categoryName}</td>
                                <td>{expense.amount}</td>
                                <td>
                                    <div className="d-flex gap-2 ">
                                        <button type="button" className="btn  btn-outline-success" onClick={() => handleExpenseUpdate(expense)}><i className="bi bi-pencil "></i></button>
                                        <button type="button" className="btn btn-outline-danger" onClick={() => handleExpenseDelete(expense)}><i className="bi bi-trash "></i></button>
                                    </div>
                                </td>
                            </tr>
                        )
                    })}
                </tbody>
            </table>
        </div>
    )
}