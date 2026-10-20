import '../HeroTable.css'

// Presentational only: give it rows, it renders them.
// row shape: { id, name, element, winRate (percent number or null), totalMatches }
function HeroTable({ rows, caption }) {
    return (
        <table className="hero-table">
            {caption && <caption>{caption}</caption>}
            <thead>
                <tr>
                    <th scope="col">Name</th>
                    <th scope="col" className="num">Win rate</th>
                    <th scope="col" className="num">Matches</th>
                </tr>
            </thead>
            <tbody>
                {rows.map((row) => (
                    <tr key={row.id} data-element={row.element}>
                        <td>
                            {row.name}
                            {/* Colour alone shouldn't carry meaning, so expose the element to screen readers */}
                            <span className="sr-only"> ({row.element})</span>
                        </td>
                        <td className="num">
                            {row.winRate === null ? '–' : `${row.winRate.toFixed(1)}%`}
                        </td>
                        <td className="num">{row.totalMatches}</td>
                    </tr>
                ))}
            </tbody>
        </table>
    )
}

export default HeroTable
