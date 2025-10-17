const BASE = (import.meta.env.VITE_API_BASE_URL as string) ?? '';

function buildUrl(path: string) {
  const base = BASE.replace(/\/$/, '');
  return base + (path.startsWith('/') ? path : `/${path}`);
}

export async function handleRes<T = unknown>(res: Response): Promise<T> {
  if (!res.ok) {
    const txt = await res.text().catch(() => '');
    throw new Error(txt || res.statusText);
  }
  // if response has no body, return undefined as unknown
  const text = await res.text().catch(() => '');
  return text ? (JSON.parse(text) as T) : (undefined as unknown as T);
}

export async function apiList<T = unknown>(resource: string): Promise<T[]> {
  const res = await fetch(buildUrl(`/api/${resource}`));
  return handleRes<T[]>(res);
}

export async function apiGet<T = unknown>(resource: string, id: number | string): Promise<T> {
  const res = await fetch(buildUrl(`/api/${resource}/${id}`));
  return handleRes<T>(res);
}

export async function apiCreate<T = unknown, P = unknown>(resource: string, payload: P): Promise<T> {
  const res = await fetch(buildUrl(`/api/${resource}`), {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  return handleRes<T>(res);
}

export async function apiUpdate<T = unknown, P = unknown>(resource: string, id: number | string, payload: P): Promise<T> {
  const res = await fetch(buildUrl(`/api/${resource}/${id}`), {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  return handleRes<T>(res);
}

export async function apiDelete(resource: string, id: number | string): Promise<void> {
  const res = await fetch(buildUrl(`/api/${resource}/${id}`), { method: 'DELETE' });
  if (!res.ok) throw new Error(await res.text().catch(() => res.statusText));
  return;
}

export async function apiQuery<T = unknown>(q: string): Promise<T[]> {
  const res = await fetch(buildUrl(`/api/query/${q}`));
  return handleRes<T[]>(res);
}