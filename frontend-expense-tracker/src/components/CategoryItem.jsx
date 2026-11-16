export default function CategoryItem({category}) {
    return (
        <div className="col-6 col-lg-2 mb-3" key={category.id}>
            <div className="card bg-dark h-100 border rounded-5" >
                <div className="card-body ">
                    <div className="ratio ratio-1x1">
                    <h5 className="card-title text-white  d-flex justify-content-center align-items-center">{category.name}</h5>
                    </div>
                </div>
            </div>
        </div>
    )
}