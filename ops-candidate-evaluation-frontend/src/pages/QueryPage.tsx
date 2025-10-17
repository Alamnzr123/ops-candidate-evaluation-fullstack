import React, { useState } from 'react';
import { apiQuery } from '../api';

export default function QueryPage() {
    const [data, setData] = useState<any[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    async function run(q: string) {
        setLoading(true); setError(null);
        try {
            const d = await apiQuery<any>(q);
            setData(Array.isArray(d) ? d : [d]);
        } catch (e: any) {
            setError(e?.message || String(e));
        } finally { setLoading(false); }
    }

    return (
        <div style={{ padding: 12 }}>
            <h2>Complex Queries</h2>
            <div style={{ marginBottom: 8 }}>
                <button onClick={() => run('q1')}>Query 1 - Cumulative Salary</button>
                <button onClick={() => run('q2')} style={{ marginLeft: 8 }}>Query 2 - Dept Analysis</button>
                <button onClick={() => run('q3')} style={{ marginLeft: 8 }}>Query 3 - Salary Ranking</button>
            </div>

            {loading && <div>Loading...</div>}
            {error && <div style={{ color: 'red' }}>{error}</div>}

            {data.length > 0 && (
                <div style={{ overflowX: 'auto', marginTop: 12 }}>
                    <table border={1} cellPadding={6} style={{ borderCollapse: 'collapse' }}>
                        <thead>
                            <tr>
                                {Object.keys(data[0]).map(k => <th key={k}>{k}</th>)}
                            </tr>
                        </thead>
                        <tbody>
                            {data.map((row, i) => (
                                <tr key={i}>
                                    {Object.keys(data[0]).map(k => <td key={k}>{String(row[k] ?? '')}</td>)}
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}