export default function MobileExpenseList({ expenseList, handleExpenseUpdate, handleExpenseDelete }) {
    return (

        <div>
            {expenseList.map((expense) => {
                return (
                    <div key={expense.id} className="card mb-3 bg-dark text-white border border-white" >
                        <div className="card-body">
                            <div className="d-flex justify-content-between mb-2">
                                <div>
                                    <h5 className="card-title">{expense.description}</h5>
                                    <h6 className="card-subtitle mb-2 text-secondary">{expense.expenseDate}</h6>
                                </div>
                                <div>
                                    <h6 className="text-success">Rs. {expense.amount}</h6>
                                </div>
                                {/* <div className="d-flex flex-column  gap-1  align-items-end ">
                                    <h6 >Rs. {expense.amount}</h6>
                                    <div className="d-flex  gap-2 ">
                                        <button type="button" className="btn btn-outline-success" onClick={() => handleExpenseUpdate(expense)}><i className="bi bi-pencil "></i></button>
                                        <button type="button" className="btn btn-outline-danger" onClick={() => handleExpenseDelete(expense)}><i className="bi bi-trash "></i></button>
                                    </div>
                                </div> */}

                            </div>
                            <div className="d-flex justify-content-between align-items-center">
                                <h5>{expense.categoryName}</h5>
                                <div className="d-flex gap-3 ">
                                    <button type="button" className="btn p-0" onClick={() => handleExpenseUpdate(expense)}><i className="bi bi-pencil text-white "></i></button>
                                    <button type="button" className="btn p-0" onClick={() => handleExpenseDelete(expense)}><i className="bi bi-trash text-danger"></i></button>
                                </div>
                            </div>

                        </div>
                    </div>
                )
            })}

        </div>
    )
}