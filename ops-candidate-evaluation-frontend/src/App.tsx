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
        <div className="app-root">
            <header className="app-header" role="navigation" aria-label="Main navigation">
                <button className="nav-button" aria-pressed={tab === 'employees'} onClick={() => setTab('employees')}>Employees</button>
                <button className="nav-button" aria-pressed={tab === 'departments'} onClick={() => setTab('departments')}>Departments</button>
                <button className="nav-button" aria-pressed={tab === 'locations'} onClick={() => setTab('locations')}>Locations</button>
                <button className="nav-button" aria-pressed={tab === 'tiers'} onClick={() => setTab('tiers')}>Tiers</button>
                <button className="nav-button" aria-pressed={tab === 'queries'} onClick={() => setTab('queries')}>Queries</button>
            </header>
            <main className="app-main">
                {tab === 'employees' && <EmployeePage />}
                {tab === 'departments' && <DepartmentPage />}
                {tab === 'locations' && <LocationPage />}
                {tab === 'tiers' && <TierPage />}
                {tab === 'queries' && <QueryPage />}
            </main>
        </div>
    );
}
