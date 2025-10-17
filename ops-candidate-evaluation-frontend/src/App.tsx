import { useState } from 'react';
import EmployeePage from './pages/EmployeePage';
import DepartmentPage from './pages/DepartmentPage';
import LocationPage from './pages/LocationPage';
import TierPage from './pages/TierPage';
import QueryPage from './pages/QueryPage';
import './App.css'

export default function App() {
    const [tab, setTab] = useState<'employees'|'departments'|'locations'|'tiers'|'queries'>('employees');
    return (
        <div style={{ fontFamily: 'Arial, sans-serif' }}>
            <header style={{ padding: 12, borderBottom: '1px solid #ddd', display: 'flex', gap: 8 }}>
                <button onClick={() => setTab('employees')}>Employees</button>
                <button onClick={() => setTab('departments')}>Departments</button>
                <button onClick={() => setTab('locations')}>Locations</button>
                <button onClick={() => setTab('tiers')}>Tiers</button>
                <button onClick={() => setTab('queries')}>Queries</button>
            </header>
            <main>
                {tab === 'employees' && <EmployeePage />}
                {tab === 'departments' && <DepartmentPage />}
                {tab === 'locations' && <LocationPage />}
                {tab === 'tiers' && <TierPage />}
                {tab === 'queries' && <QueryPage />}
            </main>
        </div>
    );
}
