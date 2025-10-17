import React from 'react';
import CrudPage, {type Column } from '../components/CrudPage';
import type { Employee } from '../types';

const columns: Column<Employee>[] = [
  { key: 'id', label: 'ID' },
  { key: 'empNo', label: 'Emp No' },
  { key: 'name', label: 'Name' },
  { key: 'deptCode', label: 'Dept Code' },
  { key: 'locationId', label: 'Location Id' },
  { key: 'position', label: 'Position' },
  { key: 'salary', label: 'Salary' },
];

export default function EmployeePage() {
  return <CrudPage<Employee> resource="employee" columns={columns} title="Employees" idField="id" />;
}