import React from 'react';
import { vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import App from '../App';

vi.mock('../pages/EmployeePage', () => ({ default: () => <div data-testid="employees">Employees</div> }));
vi.mock('../pages/DepartmentPage', () => ({ default: () => <div data-testid="departments">Departments</div> }));
vi.mock('../pages/LocationPage', () => ({ default: () => <div data-testid="locations">Locations</div> }));
vi.mock('../pages/TierPage', () => ({ default: () => <div data-testid="tiers">Tiers</div> }));
vi.mock('../pages/QueryPage', () => ({ default: () => <div data-testid="queries">Queries</div> }));

describe('App navigation', () => {
  test('renders nav buttons and default Employees tab', () => {
    render(<App />);
    expect(screen.getByRole('button', { name: /Employees/i })).toBeInTheDocument();
    expect(screen.getByTestId('employees')).toBeInTheDocument();
  });

  test('switches tabs when nav buttons are clicked', () => {
    render(<App />);

    fireEvent.click(screen.getByRole('button', { name: /Departments/i }));
    expect(screen.getByTestId('departments')).toBeInTheDocument();

    fireEvent.click(screen.getByRole('button', { name: /Locations/i }));
    expect(screen.getByTestId('locations')).toBeInTheDocument();

    fireEvent.click(screen.getByRole('button', { name: /Tiers/i }));
    expect(screen.getByTestId('tiers')).toBeInTheDocument();

    fireEvent.click(screen.getByRole('button', { name: /Queries/i }));
    expect(screen.getByTestId('queries')).toBeInTheDocument();
  });
});