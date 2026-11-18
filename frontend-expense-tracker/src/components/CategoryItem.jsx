export default function CategoryItem({ category, handleCategoryUpdate, handleCategoryDelete }) {
    return (

        <div className="col-12 col-lg-3 mb-3" >
            <div className="card bg-dark h-100 border rounded-pill" >
                <div className="card-body ">

                    <div className="d-flex  justify-content-between align-items-center  ">
                        
                            <h5 className="card-title text-white mx-1 text-truncate">{category.name}</h5>
                        

                        {category.userId &&
                            <div className="dropdown ">
                                <button className="btn btn-dark " type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                    <i className="bi bi-three-dots-vertical"></i>
                                </button>
                                <ul className="dropdown-menu bg-dark border border-white">
                                    <li><button className="dropdown-item text-primary" onClick={() => { handleCategoryUpdate(category) }}>
                                        <i className="bi bi-pencil me-2"></i>
                                        Update
                                    </button></li>
                                    <li><button className="dropdown-item text-danger" onClick={() => { handleCategoryDelete(category) }}>
                                        <i className="bi bi-trash me-2"></i>
                                        Delete
                                    </button></li>
                                </ul>
                            </div>
                        }
                    </div>

                </div>
            </div>
        </div>
    )
}