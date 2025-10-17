import React from 'react';
import CrudPage, {type Column } from '../components/CrudPage';
import type { Department } from '../types';

const columns: Column<Department>[] = [
  { key: 'id', label: 'ID' },
  { key: 'code', label: 'Code' },
  { key: 'name', label: 'Name' },
];

export default function DepartmentPage() {
  return <CrudPage<Department & Record<string, unknown>> resource="department" columns={columns} title="Departments" idField="id" />;
}