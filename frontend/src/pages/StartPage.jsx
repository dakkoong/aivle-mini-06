import NewBooksSection from "../components/NewBooksSection";
import PopularBooksSection from "../components/PopularBooksSection";

function StartPage({
  newBooks = [],
  popularBooks = [],
  onMoveToList,
  onMoveToDetail,
  onMoveToCreate,
}) {
  return (
    <>
      <main className="book-list-page">
        <section className="section-card">
          <div
            className="page-title-row"
            style={{ display: "flex", justifyContent: "flex-end" }}
          >
            <div className="list-actions">
              <button
                type="button"
                className="create-button"
                onClick={onMoveToList}
              >
                도서 목록
              </button>

              {/* <button
                type="button"
                className="create-button"
                onClick={onMoveToCreate}
              >
                <svg
                  aria-hidden="true"
                  viewBox="0 0 24 24"
                  width="20"
                  height="20"
                >
                  <path d="M12 5v14" />
                  <path d="M5 12h14" />
                </svg>
                <span>새 도서 등록</span>
              </button> */}
            </div>
          </div>

          <div
            className="main-sections-container"
            style={{
              display: "flex",
              flexDirection: "column",
              gap: "48px",
              marginTop: "32px",
            }}
          >
            <PopularBooksSection
              popularBooks={popularBooks}
              onMoveToDetail={onMoveToDetail}
            />

            <hr
              style={{
                border: "0",
                height: "1px",
                backgroundColor: "#eaeaea",
                margin: "0",
              }}
            />

            <NewBooksSection
              newBooks={newBooks}
              onMoveToDetail={onMoveToDetail}
            />
          </div>
        </section>
      </main>
    </>
  );
}

export default StartPage;
